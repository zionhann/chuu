import { useOutletContext } from "react-router-dom";
import { ProblemDetailPageData } from "../loader";
import { Collapse, CollapseProps, Divider, Flex, Typography } from "antd";
import Title from "antd/es/typography/Title";
import Paragraph from "antd/es/typography/Paragraph";

const ProblemDescriptionTab = () => {
  const data = useOutletContext() as ProblemDetailPageData;
  const examples: CollapseProps["items"] = data.testCases.map(
    (testCase, index) => ({
      key: `${index + 1}`,
      label: `Example ${index + 1}`,
      children: (
        <Flex>
          <Flex flex={"1 0 auto"} vertical>
            <Title level={5}>Input</Title>
            <Paragraph>
              <pre>{testCase.input}</pre>
            </Paragraph>
          </Flex>
          <Divider type="vertical" />
          <Flex flex={"1 0 auto"} vertical>
            <Title level={5}>Output</Title>
            <Paragraph>
              <pre>{testCase.output}</pre>
            </Paragraph>
          </Flex>
        </Flex>
      ),
    })
  );

  return (
    <Typography className="pt-8">
      <Flex justify="center">
        <Title level={3}>
          {data.problemNumber}. {data.problemName}
        </Title>
      </Flex>
      <Divider />

      <Paragraph>{data.description}</Paragraph>

      <Title level={4}>Input</Title>
      <Paragraph>{data.input}</Paragraph>

      <Title level={4}>Output</Title>
      <Paragraph>{data.output}</Paragraph>

      <Title level={4}>Example</Title>
      <Collapse items={examples} defaultActiveKey={["1"]} />
    </Typography>
  );
};

export default ProblemDescriptionTab;
