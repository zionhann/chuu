import { useLoaderData } from "react-router-dom";
import { ProblemDetailPageData } from "./loader";

export default function ProblemDetailPage() {
  const problemDetail = useLoaderData() as ProblemDetailPageData;

  return (
    <div className="pt-12">
      <h1>
        {problemDetail.problemNumber}. {problemDetail.problemName}
      </h1>
      <h2>문제 설명</h2>
      {problemDetail.description}
      <h2>입력</h2>
      입력 설명
      <h2>출력</h2>
      출력 설명
      <h2>테스트 케이스</h2>
      <table className="table-auto mx-auto w-4/5">
        <thead>
          <tr>
            <th className="border-black border-2 px-2">입력</th>
            <th className="border-black border-2 px-2">출력</th>
          </tr>
        </thead>
        <tbody>
          {problemDetail.testCases.map((testCase, caseIndex) => (
            <tr key={caseIndex}>
              <td className="border-black border-2 px-2">
                <pre className="bg-slate-100">{testCase.inputs}</pre>
              </td>
              <td className="border-black border-2 px-2">
                <pre className="bg-slate-100">{testCase.output}</pre>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <button>제출</button>
    </div>
  );
}
