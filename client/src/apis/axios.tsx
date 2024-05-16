import axios from "axios";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_SERVER_URL,
});

axiosInstance.interceptors.response.use(
  (res) => res.data,
  (err) => Promise.reject(err)
);

export default axiosInstance;
