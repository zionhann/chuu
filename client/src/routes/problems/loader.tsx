export interface ProblemListResponse {
  problemId: string;
  problemName: string;
}

const Loader = async (): Promise<Array<ProblemListResponse>> => {
  const mockres = {
    data: [
      {
        problemId: "J001",
        problemName: "Hello World",
      },
    ],
  };
  return mockres.data;
};

export default Loader;
