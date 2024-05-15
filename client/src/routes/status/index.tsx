import { Flex, Select, TableProps, Typography } from "antd";
import { Link, useSearchParams } from "react-router-dom";
import SimpleTable, { DataType } from "../../components/simpleTable";
import Search from "antd/es/input/Search";

type VerdictType = (typeof VerdictType)[keyof typeof VerdictType];
const VerdictType = {
  accepted: "Accepted",
  wrongAnswer: "Wrong answer",
  runtimeError: "Runtime error",
  compileError: "Compile error",
  running: "Running",
} as const;

const VerdictColours: { [key in VerdictType]: string } = {
  [VerdictType.accepted]: "green",
  [VerdictType.wrongAnswer]: "red",
  [VerdictType.runtimeError]: "orangered",
  [VerdictType.compileError]: "orangered",
  [VerdictType.running]: "blue",
};

interface SubmissionStatusDataType extends DataType {
  submissionId: number;
  submissionDate: string;
  username: string;
  problemId: string;
  lang: string;
  verdict: VerdictType;
}

const columns: TableProps<SubmissionStatusDataType>["columns"] = [
  {
    title: "#",
    dataIndex: "submissionId",
    key: "submissionId",
  },
  {
    title: "Date",
    dataIndex: "submissionDate",
    key: "submissionDate",
  },
  {
    title: "User",
    dataIndex: "username",
    key: "username",
  },
  {
    title: "Problem",
    dataIndex: "problemId",
    key: "problemId",
    render: (problemId) => (
      <Link to={`/problems/${problemId}`}>{problemId}</Link>
    ),
  },
  {
    title: "Lang",
    dataIndex: "lang",
    key: "lang",
  },
  {
    title: "Verdict",
    dataIndex: "verdict",
    key: "verdict",
    render: (verdict: VerdictType) => (
      <Typography.Text style={{ color: VerdictColours[verdict] }}>
        {verdict}
      </Typography.Text>
    ),
  },
];

const dataSource: SubmissionStatusDataType[] = [
  {
    key: "1",
    submissionId: 4,
    submissionDate: "2021-09-01 12:00:00",
    username: "user1",
    problemId: "J001",
    lang: "C++",
    verdict: "Accepted",
  },
  {
    key: "2",
    submissionId: 3,
    submissionDate: "2021-09-01 12:00:00",
    username: "user2",
    problemId: "J002",
    lang: "Java",
    verdict: "Wrong answer",
  },
  {
    key: "3",
    submissionId: 2,
    submissionDate: "2021-09-01 12:00:00",
    username: "user3",
    problemId: "J003",
    lang: "Java",
    verdict: "Runtime error",
  },
  {
    key: "4",
    submissionId: 1,
    submissionDate: "2021-09-01 12:00:00",
    username: "user4",
    problemId: "J004",
    lang: "C++",
    verdict: "Running",
  },
];

const SubmissionStatusPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const problemId = searchParams.get("problemId") ?? undefined;

  return (
    <Flex gap="large" vertical>
      <Flex align="center" gap="small">
        <Select
          defaultValue="problemId"
          onChange={(value) => alert(value)}
          options={[{ value: "problemId", label: "Problem" }]}
          size="large"
        />
        <Search
          placeholder="Search status"
          enterButton="Search"
          size="large"
          onSearch={(value) => alert(value)}
          defaultValue={problemId}
        />
      </Flex>
      <SimpleTable dataSource={dataSource} columns={columns} />
    </Flex>
  );
};

export default SubmissionStatusPage;
