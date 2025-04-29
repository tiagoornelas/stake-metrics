package net.stakemetrics.application.workers

import net.stakemetrics.application.annotations.SimilarityCheck
import net.stakemetrics.application.entities.FifaStrategy
import org.springframework.stereotype.Service
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.IllegalCallableAccessException

@Service
class FifaStrategySimilarityChecker {

    fun check(userStrategies: List<FifaStrategy>, strategy: FifaStrategy): FifaStrategy? {
        val similarityProperties = FifaStrategy::class.memberProperties
            .filter { it.findAnnotation<SimilarityCheck>() != null }

        return userStrategies.find { existingStrategy ->
            similarityProperties.all { property ->
                val existingValue = property.get(existingStrategy)
                val newValue = property.get(strategy)

                compareValues(existingValue, newValue)
            }
        }
    }

    private fun compareValues(existingValue: Any?, newValue: Any?): Boolean {
        if (existingValue == null && newValue == null) return true
        if (existingValue == null || newValue == null) return false

        return when {
            existingValue is Collection<*> && newValue is Collection<*> -> {
                if (existingValue.size != newValue.size) return false
                if (existingValue.isEmpty()) return true

                val existingElement = existingValue.firstOrNull { it != null }
                val newElement = newValue.firstOrNull { it != null }

                if (existingElement == null && newElement == null) return true
                if (existingElement == null || newElement == null) return false

                val hasIdProperty = hasIdProperty(existingElement::class)

                if (hasIdProperty) {
                    compareCollectionsIgnoringIds(existingValue, newValue)
                } else {
                    existingValue.containsAll(newValue) && newValue.containsAll(existingValue)
                }
            }

            hasIdProperty(existingValue::class) -> {
                compareObjectsIgnoringIds(existingValue, newValue)
            }

            else -> existingValue == newValue
        }
    }

    private fun hasIdProperty(kClass: KClass<*>): Boolean {
        return kClass.memberProperties.any { it.name == "id" }
    }

    private fun compareCollectionsIgnoringIds(collection1: Collection<*>, collection2: Collection<*>): Boolean {
        if (collection1.size != collection2.size) return false

        val list1 = collection1.toList()
        val list2 = collection2.toList()

        return list1.all { item1 ->
            if (item1 == null) {
                list1.count { it == null } == list2.count { it == null }
            } else {
                list2.any { item2 -> item2 != null && compareObjectsIgnoringIds(item1, item2) }
            }
        }
    }

    private fun compareObjectsIgnoringIds(obj1: Any, obj2: Any): Boolean {
        if (obj1::class != obj2::class) return false

        if (obj1::class.qualifiedName?.startsWith("java.time") == true) {
            return obj1 == obj2
        }

        val properties = obj1::class.memberProperties
            .filterIsInstance<KProperty1<Any, Any?>>()
            .filter { it.name != "id" }

        return properties.all { property ->
            try {
                val value1 = property.get(obj1)
                val value2 = property.get(obj2)

                compareValues(value1, value2)
            } catch (e: IllegalCallableAccessException) {
                obj1 == obj2
            }
        }
    }

}