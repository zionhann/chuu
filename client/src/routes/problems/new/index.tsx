import ExampleForm from "./exampleForm";
import BasicInfoForm from "./basicInfoForm";
import { Form } from "antd";
import { Button } from "antd";
import { Flex } from "antd";
import axiosInstance from "../../../apis/axios";
import { ProblemAddRequest } from "../../../apis";
import log from "../../../utils/log";
import { useNavigate } from "react-router-dom";

const ProblemAddPage = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const onFinish = async (requestBody: ProblemAddRequest) => {
    try {
      await axiosInstance.post("/problems", requestBody);
      alert("Problem added successfully!");
      navigate("/problems");
    } catch (err) {
      log.error("POST /problems", err);
    }
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
