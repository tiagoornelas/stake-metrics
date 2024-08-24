import {
    Button,
    Modal,
    ModalBody,
    ModalCloseButton,
    ModalContent,
    ModalFooter,
    ModalHeader,
    ModalOverlay,
    Text,
    useDisclosure
} from "@chakra-ui/react";
import useThemeColors from "hooks/useThemeColors";
import React from 'react';

const UseTerms = ({buttonText, confirmAction}: { buttonText: string, confirmAction: () => void }) => {
    const colors = useThemeColors();
    const {isOpen, onOpen, onClose} = useDisclosure();

    const handleConfirm = () => {
        confirmAction();
        onClose();
    }

    return (
        <>
            <Button bgColor={colors.product} color={colors.productContrast} onClick={onOpen}>{buttonText}</Button>

            <Modal isOpen={isOpen} onClose={onClose} size="xl">
                <ModalOverlay/>
                <ModalContent>
                    <ModalHeader>Termos de Uso</ModalHeader>
                    <ModalCloseButton/>
                    <ModalBody overflowY="auto" maxHeight="400px">
                        <Text mb={4}><strong>1. Privacidade das Estratégias do Usuário</strong><br/>
                            A Stake Metrics se compromete a nunca vender ou compartilhar as estratégias configuradas
                            pelos usuários. As estratégias criadas são de propriedade exclusiva do usuário e serão
                            tratadas com a mais alta confidencialidade.
                        </Text>
                        <Text mb={4}><strong>2. Fase de Testes e Limitações de Garantia</strong><br/>
                            A plataforma está atualmente em fase de testes, e os usuários estão cientes de que podem
                            ocorrer erros e falhas. As apostas realizadas pelos usuários são de sua inteira
                            responsabilidade, e a Stake Metrics não garante resultados positivos. O software apenas
                            aplica buscas e análises com base nos parâmetros definidos pelo próprio usuário.
                        </Text>
                        <Text mb={4}><strong>3. Condições do Serviço</strong><br/>
                            A Stake Metrics é oferecida "na forma em que está" e todas as novas funcionalidades ou
                            melhorias seguirão o fluxo de desenvolvimento estabelecido pela equipe interna. Não serão
                            desenvolvidas funcionalidades específicas para um usuário individualmente. Todas as novas
                            funcionalidades serão disponibilizadas através de planos de assinatura. Sugestões de
                            melhorias e correções serão analisadas pela equipe de desenvolvimento, que decidirá sobre a
                            implementação conforme as prioridades de projeto.
                        </Text>
                        <Text mb={4}><strong>4. Suporte ao Usuário</strong><br/>
                            O suporte ao usuário será fornecido em até dois dias úteis após a solicitação, embora a
                            Stake Metrics se esforce para responder em um prazo menor sempre que possível.
                        </Text>
                        <Text mb={4}><strong>5. Cancelamento e Reembolso</strong><br/>
                            Os usuários têm o direito de cancelar suas assinaturas a qualquer momento. Em caso de
                            cancelamento, será realizado um reembolso proporcional ao período não utilizado da
                            assinatura. A Stake Metrics também se reserva o direito de cancelar assinaturas de usuários
                            que violarem as regras de uso ou se comportarem de maneira inadequada, contrária às normas
                            de respeito e ética.
                        </Text>
                        <Text mb={4}><strong>6. Condições Gerais</strong><br/>
                            Ao utilizar a Stake Metrics, o usuário concorda com os termos estabelecidos acima. A empresa
                            se reserva o direito de modificar esses termos a qualquer momento, e as mudanças serão
                            notificadas aos usuários.
                        </Text>
                    </ModalBody>
                    <ModalFooter>
                        <Button colorScheme='blue' mr={3} onClick={onClose}>
                            Voltar
                        </Button>
                        <Button variant='ghost' onClick={handleConfirm}>Aceitar</Button>
                    </ModalFooter>
                </ModalContent>
            </Modal>
        </>
    );
};

export default UseTerms;
