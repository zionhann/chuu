import ExampleForm from "./exampleForm";
import BasicInfoForm from "./basicInfoForm";
import { Form } from "antd";
import { Button } from "antd";
import { Flex } from "antd";

const ProblemAddPage = () => {
  const [form] = Form.useForm();
  const onFinish = (values) => {
    console.log("data", values);
  };

  return (
    <Form
      form={form}
      onFinish={onFinish}
      layout="vertical"
      className="flex flex-col"
    >
      <BasicInfoForm />
      <ExampleForm />
      <Form.Item>
        <Flex justify="end">
          <Button type="primary" htmlType="submit">
            Submit
          </Button>
        </Flex>
      </Form.Item>
    </Form>
  );
};

export default ProblemAddPage;
