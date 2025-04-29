package net.stakemetrics.application.annotations

/**
 * Annotation to mark fields that should be considered in similarity checks.
 * Fields marked with this annotation will be used to determine if two entities are similar.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class SimilarityCheck
