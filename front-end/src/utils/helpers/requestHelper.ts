import axios, {AxiosResponse} from 'axios';
import {Cookies} from 'react-cookie';
import {REQUEST_TYPE} from "../interfaces";

const defaultCatch = (error: any) => {
    console.error(error);
    throw error;
}

const defaultReturn = (response: AxiosResponse<any, any>) => {
    return response.data;
}

export const makePublicRequest = (endpoint: string, method: 'GET' | 'POST' | 'PUT' | 'DELETE', data?: any) => axios({
    method: method,
    url: endpoint,
    data: data
})
    .then(defaultReturn)
    .catch(defaultCatch);

export const makeRequest = async (endpoint: string, method: REQUEST_TYPE, data?: any) => {
    const cookies = new Cookies();
    const token = cookies.get('token');

    try {
        const response = await axios({
            method: method,
            url: endpoint,
            data: data,
            headers: {"Authorization": `Bearer ${token}`}
        });
        return defaultReturn(response);
    } catch (error) {
        return defaultCatch(error);
    }
};