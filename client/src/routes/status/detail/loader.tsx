import axiosInstance from "../../../apis/axios";

export interface StatusDetailResponse {
  solutionId: number;
  sourceCode: string[];
  report: string;
}

const Loader = async ({ params }) => {
  const { solutionId } = params;
  const res: { data: StatusDetailResponse[] } = await axiosInstance.get(
    `/solutions/${solutionId}/status`
  );
  return res.data[0];
};

export default Loader;
