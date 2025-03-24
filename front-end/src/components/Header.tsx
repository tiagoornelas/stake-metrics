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
  Skeleton,
  SkeletonCircle,
  Stack,
  Tag,
  useColorMode,
  useColorModeValue,
  useMediaQuery,
} from "@chakra-ui/react";
import BetaTag from "components/BetaTag";
import ProductOnHeader from "components/ProductOnHeader";
import { useUserDispatch, useUserState } from "context/UserContext";
import { useErrorToast } from "hooks/useErrorToast";
import { Dispatch, ReactNode } from "react";
import { useCookies } from "react-cookie";
import { FaMoon, FaSun } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { APP_NAVIGATION } from "utils/constants/navigationConstants";
import { SUCCESS_TYPES } from "utils/constants/successConstants";
import { cleanUser } from "utils/helpers/contextHelper";
import { hasFeature } from "utils/helpers/featureHelper";
import {
  NavigationLinkOnHeaderValue,
  NavigationModuleTypes,
  UserReducerAction,
} from "utils/interfaces";

interface Props {
  children: ReactNode;
  path: string;
}

const NavLink = (props: Props) => {
  const { children, path } = props;
  const navigate = useNavigate();
  const bgColor = useColorModeValue("gray.300", "gray.700");

  return (
    <Box
      as="a"
      px={2}
      py={1}
      rounded={"md"}
      _hover={{
        textDecoration: "none",
        bg: bgColor,
      }}
      cursor={"pointer"}
      onClick={() => navigate(`/${path.toLowerCase()}`, { replace: true })}
    >
      {children}
    </Box>
  );
};

const NavigationTag = ({
  type,
  moduleText,
  moduleColor,
}: {
  type: NavigationModuleTypes;
  moduleText: string;
  moduleColor: string;
}) => {
  switch (type) {
    case NavigationModuleTypes.BETA:
      return <BetaTag ml={2} />;
    case NavigationModuleTypes.COMING_SOON:
      return (
        <Tag ml={2} colorScheme={"blue"}>
          Em breve
        </Tag>
      );
    default:
      return (
        <Tag ml={2} colorScheme={moduleColor}>
          {moduleText}
        </Tag>
      );
  }
};

const Header = () => {
  const { colorMode, toggleColorMode } = useColorMode();
  const [, , removeCookie] = useCookies(["userId", "token"]);
  const [isSmallerThanLg] = useMediaQuery("(max-width: 62em)");
  const navigate = useNavigate();
  const { user } = useUserState();
  const userDispatch: Dispatch<UserReducerAction> = useUserDispatch();

  const getLoggedUserNames = (): Array<String> => (user.name || "").split(" ");
  const getAvatarSource = (): string =>
    `https://ui-avatars.com/api/?name=${getLoggedUserNames().join("+")}`;

  const logOut = useErrorToast(async () => {
    cleanUser(userDispatch);
    removeCookie("userId");
    removeCookie("token");
    setTimeout(() => {
      navigate("/", { replace: true });
    }, 500);
  }, SUCCESS_TYPES.USER_LOGGED_OUT);

  return (
    <>
      <Box bg={useColorModeValue("gray.100", "gray.900")} px={4}>
        <Flex h={16} alignItems={"center"} justifyContent={"space-between"}>
          <ProductOnHeader path={"/app"} />
          <HStack as={"nav"} spacing={4} display={{ base: "none", lg: "flex" }}>
            {APP_NAVIGATION.map(
              ({
                name,
                path,
                moduleText,
                moduleColor,
                type,
              }: NavigationLinkOnHeaderValue) => {
                return (
                  <NavLink key={path} path={path.toLowerCase()}>
                    {name}
                    <NavigationTag
                      type={type}
                      moduleColor={moduleColor}
                      moduleText={moduleText}
                    />
                  </NavLink>
                );
              }
            )}
          </HStack>

          <Flex alignItems={"center"}>
            <Stack direction={"row"} spacing={7}>
              <Button onClick={toggleColorMode}>
                {colorMode === "light" ? <FaMoon /> : <FaSun />}
              </Button>

              <Menu>
                <MenuButton
                  as={Button}
                  rounded={"full"}
                  variant={"link"}
                  cursor={"pointer"}
                  minW={0}
                >
                  <SkeletonCircle isLoaded={!!user.name}>
                    <Avatar size={"sm"} src={getAvatarSource()} />
                  </SkeletonCircle>
                </MenuButton>
                <MenuList alignItems={"center"}>
                  <br />
                  <Center>
                    <Avatar size={"xl"} src={getAvatarSource()} />
                  </Center>
                  <br />
                  <Center>
                    <p>{user.name}</p>
                  </Center>
                  <br />
                  <MenuDivider />
                  {isSmallerThanLg && (
                    <Flex direction={"column"} gap={1}>
                      {APP_NAVIGATION.map(
                        ({
                          name,
                          path,
                          moduleText,
                          moduleColor,
                          type,
                          feature,
                        }: NavigationLinkOnHeaderValue) => {
                          const canAccess = feature
                            ? hasFeature(user, feature)
                            : true;

                          return (
                            <Skeleton
                              key={path}
                              isLoaded={!!user.subscription?.features}
                              h={8}
                            >
                              <MenuItem
                                onClick={() =>
                                  canAccess &&
                                  navigate(`/${path.toLowerCase()}`, {
                                    replace: true,
                                  })
                                }
                                isDisabled={!canAccess}
                              >
                                {name}
                                <NavigationTag
                                  type={type}
                                  moduleColor={moduleColor}
                                  moduleText={moduleText}
                                />
                              </MenuItem>
                            </Skeleton>
                          );
                        }
                      )}
                      <MenuDivider />
                    </Flex>
                  )}
                  <MenuItem onClick={() => navigate("/app/user-management")}>
                    Minha conta
                  </MenuItem>
                  <MenuItem onClick={logOut}>Sair</MenuItem>
                </MenuList>
              </Menu>
            </Stack>
          </Flex>
        </Flex>
      </Box>
    </>
  );
};

export default Header;
