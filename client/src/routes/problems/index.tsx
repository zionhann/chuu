import { Link, useLoaderData, useOutletContext } from "react-router-dom";
import { ProblemListResponse } from "./loader";
import SimpleTable from "../../components/simpleTable";
import { DataType } from "../../components/simpleTable";
import { Flex, TableProps, Typography } from "antd";
import { Button } from "antd";
import PageKeys from "../../constants/pageKeys";
import React, { useEffect } from "react";
import { PlusOutlined } from "@ant-design/icons";

interface ProblemListDataType extends DataType {
  problemNumber: string;
  problemName: string;
}

const columns: TableProps<ProblemListDataType>["columns"] = [
  {
    title: "#",
    dataIndex: "problemNumber",
    key: "problemNumber",
  },
  {
    title: "Name",
    dataIndex: "problemName",
    key: "problemName",
    render: (name, { problemNumber }) => (
      <Typography.Text>
        <Link to={`${problemNumber}`}>{name}</Link>
      </Typography.Text>
    ),
  },
];

const ProblemListPage = () => {
  const res = useLoaderData() as ProblemListResponse;
  const dataSource: ProblemListDataType[] = res.data.map((data, index) => {
    return {
      key: index.toString(),
      ...data,
    };
  });
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
