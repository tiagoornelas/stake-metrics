import {
  Box,
  Button,
  Flex,
  useColorMode,
  useColorModeValue,
} from "@chakra-ui/react";
import ProductOnHeader from "components/ProductOnHeader";
import { useEnvironmentSettings } from "hooks/useEnvironmentSettings";
import React, { Fragment } from "react";
import { useCookies } from "react-cookie";
import { FaMoon, FaSun } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

export const PublicHeader = () => {
  const { colorMode, toggleColorMode } = useColorMode();
  const [cookie] = useCookies(["userId", "token"]);
  const isUserLoggedIn = cookie.userId && cookie.token;
  const navigate = useNavigate();

  const { offLineMode } = useEnvironmentSettings();
  const bgColor = useColorModeValue("gray.100", "gray.900");

  if (offLineMode)
    return (
      <>
        <Box bg={bgColor} px={4}>
          <Flex h={16} alignItems={"center"} justifyContent={"space-between"}>
            <ProductOnHeader />

            <Flex alignItems={"center"} gap={2}>
              <Button onClick={toggleColorMode}>
                {colorMode === "light" ? <FaMoon /> : <FaSun />}
              </Button>
              <Button
                colorScheme="yellow"
                onClick={() => navigate("/login", { replace: true })}
              >
                Login
              </Button>
            </Flex>
          </Flex>
        </Box>
      </>
    );

  return (
    <>
      <Box bg={bgColor} px={4}>
        <Flex h={16} alignItems={"center"} justifyContent={"space-between"}>
          <ProductOnHeader />

          <Flex alignItems={"center"} gap={2}>
            <Button onClick={toggleColorMode}>
              {colorMode === "light" ? <FaMoon /> : <FaSun />}
            </Button>
            {isUserLoggedIn ? (
              <Button
                colorScheme="yellow"
                onClick={() => navigate("/app", { replace: true })}
              >
                Acessar plataforma
              </Button>
            ) : (
              <Fragment>
                <Button
                  variant="outline"
                  colorScheme="yellow"
                  onClick={() => navigate("/create-account", { replace: true })}
                >
                  Criar conta
                </Button>
                <Button
                  colorScheme="yellow"
                  onClick={() => navigate("/login", { replace: true })}
                >
                  Login
                </Button>
              </Fragment>
            )}
          </Flex>
        </Flex>
      </Box>
    </>
  );
};

export default PublicHeader;
