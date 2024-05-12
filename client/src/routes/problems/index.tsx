import { Link, useLoaderData, useOutletContext } from "react-router-dom";
import { ProblemListResponse } from "./loader";
import SimpleTable from "../../components/simpleTable";
import { DataType } from "../../components/simpleTable";
import { Flex, TableProps } from "antd";
import { Button } from "antd";
import PageKeys from "../../constants/pageKeys";
import React, { useEffect } from "react";
import { PlusOutlined } from "@ant-design/icons";

interface ProblemListDataType extends DataType {
  problemId: string;
  problemName: string;
}

const columns: TableProps<ProblemListDataType>["columns"] = [
  {
    title: "#",
    dataIndex: "problemId",
    key: "problemId",
  },
  {
    title: "Name",
    dataIndex: "problemName",
    key: "problemName",
    render: (name, { problemId }) => <Link to={`${problemId}`}>{name}</Link>,
  },
];

const dataSource: ProblemListDataType[] = [
  {
    key: "1",
    problemId: "J001",
    problemName: "Hello World",
  },
  {
    key: "2",
    problemId: "J002",
    problemName: "A + B",
  },
  {
    key: "3",
    problemId: "J003",
    problemName: "A - B",
  },
];

const ProblemListPage = () => {
  const data = useLoaderData() as ProblemListResponse[];
  const setCurrentPage = useOutletContext() as React.Dispatch<
    React.SetStateAction<(typeof PageKeys)[keyof typeof PageKeys]>
  >;
  useEffect(() => setCurrentPage(PageKeys.PROBLEMS), [setCurrentPage]);

  return (
    <>
      <Flex justify="end" className="pb-4">
        <Link to="new">
          <Button icon={<PlusOutlined />} className="border-0" size="large" />
        </Link>
      </Flex>
      <SimpleTable columns={columns} dataSource={dataSource} />
    </>
  );
};

export default ProblemListPage;
