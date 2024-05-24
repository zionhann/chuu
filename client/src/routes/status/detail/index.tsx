import { useLoaderData } from "react-router-dom";
import { StatusDetailResponse } from "./loader";
import CodeMirror from "@uiw/react-codemirror";
import { java } from "@codemirror/lang-java";
import { Space, Typography } from "antd";

const StatusDetailPage = () => {
  const res = useLoaderData() as StatusDetailResponse;

  return (
    <>
      {res.sourceCode.map((sourceCode, index) => (
        <Space key={index}>
          <CodeMirror
            extensions={[java()]}
            height="50vh"
            value={sourceCode}
            readOnly
          />
        </Space>
      ))}
      <Typography>
        <pre>{res.report}</pre>
      </Typography>
    </>
  );
};

export default StatusDetailPage;
