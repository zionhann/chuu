import axiosInstance from "../../../apis/axios";
import log from "../../../utils/log";

export interface ProblemDetailPageData {
  problemNumber: string;
  problemName: string;
  description: string;
  input: string;
  output: string;
  testCases: {
    input: string;
    output: string;
  }[];
}

const Loader = async ({ params }) => {
  const { problemNumber } = params;

  try {
    const res: { data: ProblemDetailPageData[] } = await axiosInstance.get(
      `/problems/${problemNumber}`
    );
    log.info("GET /problems/:problemNumber", res);
    return res.data[0];
  } catch (err) {
    log.error("GET /problems/:problemNumber", err);
  }
};

export default Loader;
