import { Button, Divider, Flex, Form, Input, Select, Typography } from "antd";
import Title from "antd/es/typography/Title";
import { useNavigate, useOutletContext } from "react-router-dom";
import { ProblemDetailPageData } from "../loader";
import { useState } from "react";
import CodeMirror, { Extension } from "@uiw/react-codemirror";
import { java } from "@codemirror/lang-java";
import axiosInstance from "../../../../apis/axios";
import log from "../../../../utils/log";

type ExtensionMap = { [key: string]: Extension[] | undefined };

const Extensions: ExtensionMap = {
  NONE: undefined,
  JAVA: [java()],
  // TODO: Support C++
};

interface OnSubmitResponse {
  data: {
    solutionId: number;
  }[];
}

const SolutionSubmitTab = () => {
  const { problemNumber, problemName } =
    useOutletContext() as ProblemDetailPageData;
  const [lang, setLanguage] = useState("NONE");
  const navigate = useNavigate();

  const onSubmit = async (values) => {
    try {
      log.info(`POST /solutions/${problemNumber}`, values);
      const res: OnSubmitResponse = await axiosInstance.post(
        `/solutions/${problemNumber}`,
        values
      );
      const solutionId = res.data[0].solutionId;
      axiosInstance.post(`/solutions/${solutionId}/grade`);
      navigate(`/status?problemNumber=${problemNumber}`);
    } catch (error) {
      log.error(`POST /solutions/${problemNumber}`, error);
    }
  };

  return (
    <Typography className="pt-8">
      <Flex justify="center">
        <Title level={3}>
          {problemNumber}. {problemName}
        </Title>
      </Flex>
      <Divider />

      <Form onFinish={onSubmit} className="flex flex-col" layout="vertical">
        <Form.Item
          label="Author"
          name="author"
          wrapperCol={{ span: 5 }}
          rules={[{ required: true, message: "Please input!" }]}
        >
          <Input placeholder="Name(ID Number)" />
        </Form.Item>
        <Form.Item
          label="Language"
          name="language"
          wrapperCol={{ span: 5 }}
          rules={[{ required: true, message: "Please input!" }]}
        >
          <Select
            defaultValue={"Select Language"}
            onSelect={(selected) => setLanguage(selected)}
            options={[
              {
                label: "Java",
                value: "JAVA",
              },
            ]}
          />
        </Form.Item>
        <Form.Item
          label="Source Code"
          name="sourceCode"
          rules={[{ required: true, message: "Please input!" }]}
        >
          <CodeMirror
            extensions={Extensions[lang]}
            placeholder="Your code here"
            height="50vh"
          />
        </Form.Item>
        <Form.Item>
          <Flex justify="end">
            <Button type="primary" htmlType="submit">
              Submit
            </Button>
          </Flex>
        </Form.Item>
      </Form>
    </Typography>
  );
};

export default SolutionSubmitTab;
