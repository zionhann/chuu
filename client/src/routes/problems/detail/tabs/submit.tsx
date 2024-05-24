import {
  Button,
  Divider,
  Flex,
  Form,
  Input,
  Select,
  Tabs,
  Typography,
} from "antd";
import Title from "antd/es/typography/Title";
import { useNavigate, useOutletContext } from "react-router-dom";
import { ProblemDetailPageData } from "../loader";
import { useState } from "react";
import CodeMirror, { Extension } from "@uiw/react-codemirror";
import { java } from "@codemirror/lang-java";
import axiosInstance from "../../../../apis/axios";
import log from "../../../../utils/log";
import Dragger from "antd/es/upload/Dragger";
import {
  CodeOutlined,
  FolderAddOutlined,
  FolderOutlined,
  PlusOutlined,
} from "@ant-design/icons";

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

type TabKeys = (typeof TabKeys)[keyof typeof TabKeys];
const TabKeys = {
  sourceCode: "SOURCE_CODE",
  folder: "FOLDER",
} as const;

const SolutionSubmitTab = () => {
  const { problemNumber, problemName } =
    useOutletContext() as ProblemDetailPageData;

  const [lang, setLanguage] = useState("NONE");
  const [isHovered, setIsHovered] = useState(false);
  const [selectedTabKey, setSelectedTabKey] = useState<TabKeys>(
    TabKeys.sourceCode
  );

  const navigate = useNavigate();

  const onSubmit = async (formValues) => {
    try {
      log.info(`POST /solutions/${problemNumber}`, formValues);
      const { author, language, sourceFiles, sourceCode } = formValues;
      const formData = new FormData();

      formData.append("author", author);
      formData.append("language", language);

      if (selectedTabKey === TabKeys.sourceCode) {
        formData.append("sourceCode", sourceCode);
      } else {
        sourceFiles?.map((file) =>
          formData.append("sourceFiles", file.originFileObj)
        );
      }
      const res: OnSubmitResponse = await axiosInstance.post(
        `/solutions/${problemNumber}`,
        formData
      );
      const solutionId = res.data[0].solutionId;
      axiosInstance.post(`/solutions/${solutionId}/grade`);
      navigate(`/status?problemNumber=${problemNumber}`);
    } catch (error) {
      log.error(`POST /solutions/${problemNumber}`, error);
    }
  };

  const sourceOptions = [
    {
      key: TabKeys.sourceCode,
      label: "Code",
      children: (
        <Form.Item
          name="sourceCode"
          rules={[
            {
              required: selectedTabKey === TabKeys.sourceCode,
              message: "Please input!",
            },
          ]}
        >
          <CodeMirror
            extensions={Extensions[lang]}
            placeholder="Your code here"
            height="50vh"
          />
        </Form.Item>
      ),
      icon: <CodeOutlined />,
    },
    {
      key: TabKeys.folder,
      label: "Folder",
      children: (
        <div
          onDragOver={() => setIsHovered(true)}
          onDragLeave={() => setIsHovered(false)}
          onDrop={() => setIsHovered(false)}
        >
          <Form.Item
            name="sourceFiles"
            valuePropName="fileList"
            getValueFromEvent={(e) => e.fileList}
            initialValue={[]}
            rules={[
              {
                required: selectedTabKey === TabKeys.folder,
                message: "Please input!",
              },
            ]}
          >
            <Dragger beforeUpload={() => false} directory multiple>
              {isHovered ? (
                <p className="ant-upload-drag-icon">
                  <PlusOutlined />
                </p>
              ) : (
                <>
                  <p className="ant-upload-drag-icon">
                    <FolderAddOutlined />
                  </p>
                  <p className="ant-upload-text">
                    Click or drag folder to upload
                  </p>
                </>
              )}
            </Dragger>
          </Form.Item>
        </div>
      ),
      icon: <FolderOutlined />,
    },
  ];

  return (
    <>
      <Typography className="pt-8">
        <Flex justify="center">
          <Title level={3}>
            {problemNumber}. {problemName}
          </Title>
        </Flex>
        <Divider />
      </Typography>

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
        <Tabs
          defaultActiveKey={selectedTabKey}
          items={sourceOptions}
          onChange={(key) => setSelectedTabKey(key as TabKeys)}
        />
        <Form.Item>
          <Flex justify="end">
            <Button type="primary" htmlType="submit">
              Submit
            </Button>
          </Flex>
        </Form.Item>
      </Form>
    </>
  );
};

export default SolutionSubmitTab;
