export const useEnvironmentSettings = () => {
    const offLineMode = process.env.REACT_APP_OFF_LINE_MODE === "true";
    const acceptingNewCustomers = process.env.REACT_APP_DISABLE_NEW_USERS !== "true";

    return { offLineMode, acceptingNewCustomers };
}