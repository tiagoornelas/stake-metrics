import {Box, Container, IconButton, Stack, Text} from '@chakra-ui/react'
import {FiX} from 'react-icons/fi'

interface Props {
    title: string;
    titleColor: string;
    description: string;
    hideCallback?: () => void;
}

const Banner = ({title, description, hideCallback, titleColor}: Props) => (
    <Box as="section" pb="8">
        <Box borderBottomWidth="1px" bg="bg.surface">
            <Container py={{base: '4', md: '3.5'}}>
                <Stack
                    direction="row"
                    spacing={{base: '3', md: '4'}}
                    justify="center"
                    align="center"
                    textAlign="center"
                >
                    <Box>
                        <Text fontWeight="medium" color={titleColor}>{title}</Text>
                        <Text color="fg.muted">
                            {description}
                        </Text>
                    </Box>
                    {!!hideCallback && <IconButton icon={<FiX/>} variant="tertiary" aria-label="Close banner"
                                                   onClick={hideCallback}/>}
                </Stack>
            </Container>
        </Box>
    </Box>
)

export default Banner;
