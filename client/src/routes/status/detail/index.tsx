import { useLoaderData } from "react-router-dom";
import { StatusDetailResponse } from "./loader";
import CodeMirror from "@uiw/react-codemirror";
import { java } from "@codemirror/lang-java";
import { Flex, Typography } from "antd";

const StatusDetailPage = () => {
  const res = useLoaderData() as StatusDetailResponse;

  return res.sourceCode.map((sourceCode, index) => (
    <Flex key={index} vertical>
      <CodeMirror
        extensions={[java()]}
        height="50vh"
        value={sourceCode}
        readOnly
      />
      <Typography>
        <pre>{res.report}</pre>
      </Typography>
    </Flex>
  ));
};

export default StatusDetailPage;
