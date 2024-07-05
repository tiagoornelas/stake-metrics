import {
    Avatar,
    Box,
    Button,
    Center,
    Flex,
    HStack,
    Menu,
    MenuButton,
    MenuDivider,
    MenuItem,
    MenuList,
    Stack,
    useColorMode,
    useColorModeValue
} from '@chakra-ui/react';
import {FaMoon, FaSun} from "react-icons/fa";
import ProductLogo from "components/ProductLogo";
import {useCookies} from "react-cookie";
import {Dispatch, ReactNode} from "react";
import {useUserState, useUserDispatch} from "context/UserContext";
import ProductName from "components/ProductName";
import {APP_NAVIGATION} from "utils/constants/navigationConstants";
import {NavigationLinkOnHeaderValue, UserContext, UserReducerAction} from "utils/interfaces";
import {cleanUser} from "utils/helpers/contextHelper";

interface Props {
    children: ReactNode,
    path: string
}

const NavLink = (props: Props) => {
    const {children, path} = props

    return (
        <Box
            as="a"
            px={2}
            py={1}
            rounded={'md'}
            _hover={{
                textDecoration: 'none',
                bg: useColorModeValue('gray.200', 'gray.700'),
            }}
            href={path}>
            {children}
        </Box>
    )
}

export const Header = () => {
    const {colorMode, toggleColorMode} = useColorMode();
    const [, , removeCookie] = useCookies(["userId", "token"]);

    const userContext: UserContext = useUserState();
    const userDispatch: Dispatch<UserReducerAction> = useUserDispatch();

    const getLoggedUserNames = (): Array<String> => (userContext.user.name || "").split(" ");

    const getAvatarSource = (): string => `https://ui-avatars.com/api/?name=${getLoggedUserNames().join("+")}`

    const logOut = () => {
        cleanUser(userDispatch);
        removeCookie("userId");
        removeCookie("token");
        window.location.reload();
    };

    const goToHomePage = () => window.location.assign("/");

    return (
        <>
            <Box bg={useColorModeValue('gray.100', 'gray.900')} px={4}>
                <Flex h={16} alignItems={'center'} justifyContent={'space-between'}>
                    <Flex direction="row" alignItems={'center'} gap={2}
                          cursor="pointer"
                          onClick={goToHomePage}><ProductLogo/><b><ProductName/></b></Flex>
                    <HStack as={'nav'} spacing={4} display={{base: 'none', md: 'flex'}}>
                        {APP_NAVIGATION.map(({name, path}: NavigationLinkOnHeaderValue) => (
                            <NavLink key={path} path={path.toLowerCase()}>{name}</NavLink>
                        ))}
                    </HStack>

                    <Flex alignItems={'center'}>
                        <Stack direction={'row'} spacing={7}>
                            <Button onClick={toggleColorMode}>
                                {colorMode === 'light' ? <FaMoon/> : <FaSun/>}
                            </Button>

                            <Menu>
                                <MenuButton
                                    as={Button}
                                    rounded={'full'}
                                    variant={'link'}
                                    cursor={'pointer'}
                                    minW={0}>
                                    <Avatar
                                        size={'sm'}
                                        src={getAvatarSource()}
                                    />
                                </MenuButton>
                                <MenuList alignItems={'center'}>
                                    <br/>
                                    <Center>
                                        <Avatar
                                            size={'xl'}
                                            src={getAvatarSource()}
                                        />
                                    </Center>
                                    <br/>
                                    <Center>
                                        <p>{userContext.user.name}</p>
                                    </Center>
                                    <br/>
                                    <MenuDivider/>
                                    <MenuItem onClick={() => window.location.assign("/user-management")}>Minha
                                        conta</MenuItem>
                                    <MenuItem onClick={logOut}>Sair</MenuItem>
                                </MenuList>
                            </Menu>
                        </Stack>
                    </Flex>
                </Flex>
            </Box>
        </>
    )
}

export default Header;
