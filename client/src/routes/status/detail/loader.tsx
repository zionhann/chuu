import axiosInstance from "../../../apis/axios";

export interface StatusDetailResponse {
  solutionId: number;
  sourceCode: string;
  report: string;
}

const Loader = async ({ params }) => {
  const { solutionId } = params;
  //   const res: { data: StatusDetailResponse[] } = await axiosInstance.get(
  //     `/status/${solutionId}`
  //   );
  return {
    solutionId: 1,
    sourceCode:
      'class Main { \n\tpublic static void main(String[] args) { \n\t\tSystem.out.println("Hello, World!"); \n\t} \n}',
    report: `[Test case 1]
                    
    Input:
    johndoe
    12
    John Doe
                        
    Expected:
    Error! password is too short.
                        
    Actual:
    
    J031
    Error! password is too short.
    
    Result: Passed
    ============
    [Test case 2]
                        
    Input:
    johndoe
    1234
    John Doe
                        
    Expected:
    User Id: johndoe
    Password: 12**
    User Name: John Doe
                        
    Actual:
    
    J031
    User Id: johndoe
    Password: 12**
    User Name: John Doe
    
    Result: Passed
    ============
    `,
  };
};

export default Loader;
