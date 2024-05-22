import { useLoaderData } from "react-router-dom";
import { StatusDetailResponse } from "./loader";
import CodeMirror from "@uiw/react-codemirror";
import { java } from "@codemirror/lang-java";
import { Flex, Typography } from "antd";

const StatusDetailPage = () => {
  const res = useLoaderData() as StatusDetailResponse;

  return (
    <Flex vertical>
      <CodeMirror
        extensions={[java()]}
        height="50vh"
        value={res.sourceCode}
        readOnly
      />
      <Typography>
        <pre>{res.report}</pre>
      </Typography>
    </Flex>
  );
};

export default StatusDetailPage;
