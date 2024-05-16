export interface ProblemAddRequest {
  problemCode: string;
  problemName: string;
  description: string;
  inputDescription: string;
  outputDescription: string;
  testCases: {
    input: string[];
    output: string;
  }[];
}
