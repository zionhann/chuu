import { Flex, Select, TableProps, Typography } from "antd";
import { Link, useLoaderData } from "react-router-dom";
import SimpleTable, { DataType } from "../../components/simpleTable";
import Search from "antd/es/input/Search";
import { SolutionListResponse } from "./loader";
import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import log from "../../utils/log";

type VerdictType = (typeof VerdictType)[keyof typeof VerdictType];
const VerdictType = {
  accepted: "ACCEPTED",
  wrongAnswer: "WRONG_ANSWER",
  runtimeError: "RUNTIME_ERROR",
  compileError: "COMPILE_ERROR",
  running: "RUNNING",
  pending: "PENDING",
} as const;

const VerdictColours: { [key in VerdictType]: string } = {
  [VerdictType.accepted]: "green",
  [VerdictType.wrongAnswer]: "red",
  [VerdictType.runtimeError]: "orangered",
  [VerdictType.compileError]: "orangered",
  [VerdictType.running]: "blue",
  [VerdictType.pending]: "gray",
};

interface SubmissionStatusDataType extends DataType {
  solutionId: number;
  submissionDate: string;
  author: string;
  problemNumber: string;
  lang: string;
  verdict: VerdictType;
}

const columns: TableProps<SubmissionStatusDataType>["columns"] = [
  {
    title: "#",
    dataIndex: "solutionId",
    key: "solutionId",
  },
  {
    title: "Date",
    dataIndex: "submissionDate",
    key: "submissionDate",
  },
  {
    title: "Author",
    dataIndex: "author",
    key: "author",
  },
  {
    title: "Problem",
    dataIndex: "problemNumber",
    key: "problemNumber",
    render: (problemNumber) => (
      <Link to={`/problems/${problemNumber}`}>{problemNumber}</Link>
    ),
  },
  {
    title: "Lang",
    dataIndex: "language",
    key: "lang",
  },
  {
    title: "Verdict",
    dataIndex: "verdict",
    key: "verdict",
    render: (verdict: VerdictType, record) =>
      verdict !== VerdictType.pending && verdict !== VerdictType.running ? (
        <Link to={`/status/${record.solutionId}`}>
          <Typography.Text style={{ color: VerdictColours[verdict] }} underline>
            {verdict}
          </Typography.Text>
        </Link>
      ) : (
        <Typography.Text style={{ color: VerdictColours[verdict] }}>
          {verdict}
        </Typography.Text>
      ),
  },
];

interface SolutionLoaderType {
  res: SolutionListResponse;
  problemNumber: string | undefined;
}

interface MessageDataType {
  solutionId: number;
  verdict: VerdictType;
}

const SolutionStatusPage = () => {
  const { res, problemNumber } = useLoaderData() as SolutionLoaderType;
  const [dataSource, setDataSource] = useState(
    res.data.map((data, index) => {
      return {
        key: index.toString(),
        ...data,
      };
    })
  );

  useEffect(() => {
    const client = new Client({
      brokerURL: `${import.meta.env.VITE_WS_SERVER_URL}/solution`,
      onConnect: () => {
        client.subscribe("/topic/status", (message) => {
          const data: MessageDataType = JSON.parse(message.body);
          log.info("Received message", data);

          setDataSource((prev) =>
            prev.map((prevData) => {
              if (prevData.solutionId === data.solutionId) {
                log.info("Update verdict", data);
                return {
                  ...prevData,
                  verdict: data.verdict,
                };
              }
              return prevData;
            })
          );
        });
      },
    });
    client.activate();
    log.info("Initiates WebSocket connection", client);
  }, []);

  return (
    <Flex gap="large" vertical>
      <Flex align="center" gap="small">
        <Select
          defaultValue="problemNumber"
          onChange={(value) => alert(value)}
          options={[{ value: "problemNumber", label: "Problem" }]}
          size="large"
        />
        <Search
          placeholder="Search status"
          enterButton="Search"
          size="large"
          onSearch={(value) => alert(value)}
          defaultValue={problemNumber}
        />
      </Flex>
      <SimpleTable dataSource={dataSource} columns={columns} />
    </Flex>
  );
};

export default SolutionStatusPage;
