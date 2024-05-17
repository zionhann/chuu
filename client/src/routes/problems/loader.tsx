import axiosInstance from "../../apis/axios";
import log from "../../utils/log";

export interface ProblemListResponse {
  data: {
    problemNumber: string;
    problemName: string;
  }[];
}

const Loader = async () => {
  try {
    const res: ProblemListResponse = await axiosInstance.get("/problems");
    log.info("GET /problems", res);
    return res;
  } catch (err) {
    log.error("GET /problems", err);
  }
};

export default Loader;
