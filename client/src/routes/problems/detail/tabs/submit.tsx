import { Button, Divider, Flex, Form, Input, Select, Typography } from "antd";
import Title from "antd/es/typography/Title";
import { useOutletContext } from "react-router-dom";
import { ProblemDetailPageData } from "../loader";
import { useState } from "react";
import CodeMirror, { Extension } from "@uiw/react-codemirror";
import { java } from "@codemirror/lang-java";

type ExtensionMap = { [key: string]: Extension[] | undefined };

const Extensions: ExtensionMap = {
  none: undefined,
  java: [java()],
  // TODO: Support C++
};

const SolutionSubmitTab = () => {
  const { problemNumber: problemId, problemName } =
    useOutletContext() as ProblemDetailPageData;
  const [lang, setLanguage] = useState("none");

  const onFinish = (values) => {
    console.log("data", values);
  };

  return (
    <Typography className="pt-8">
      <Flex justify="center">
        <Title level={3}>
          {problemId}. {problemName}
        </Title>
      </Flex>
      <Divider />

      <Form onFinish={onFinish} className="flex flex-col" layout="vertical">
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
                value: "java",
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
