export interface ProblemAddRequest {
  number: string;
  name: string;
  description: string;
  inputDescription: string;
  outputDescription: string;
  testCases: {
    input: string[];
    output: string;
  }[];
}
