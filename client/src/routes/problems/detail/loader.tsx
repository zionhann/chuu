export interface ProblemDetailPageData {
  problemId: string;
  problemName: string;
  description: string;
  input: string;
  output: string;
  testCases: {
    inputs: string[];
    output: string;
  }[];
}

const Loader = async (): Promise<ProblemDetailPageData> => {
  const mockres = {
    data: [
      {
        problemId: "J001",
        problemName: "Sum",
        description: "Add two number and return the result.",
        input: "Two integers A and B (0 <= A, B <= 10000)",
        output: "Return the sum of A and B.",
        testCases: [
          {
            inputs: ["1", "2"],
            output: "3",
          },
          {
            inputs: ["2", "3"],
            output: "5",
          },
          {
            inputs: ["3", "4"],
            output: "7",
          },
        ],
      },
    ],
  };
  return mockres.data[0];
};

export default Loader;
