import { Form, Input } from "antd";

const BasicInfoForm = () => {
  return (
    <>
      <Form.Item
        label="Problem ID"
        name="problemId"
        rules={[{ required: true, message: "Please input!" }]}
        wrapperCol={{ span: 5 }}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label="Problem Name"
        name="problemName"
        rules={[{ required: true, message: "Please input!" }]}
      >
        <Input />
      </Form.Item>

      <Form.Item
        label="Description"
        name="description"
        rules={[{ required: true, message: "Please input!" }]}
      >
        <Input.TextArea style={{ resize: "none" }} rows={7} />
      </Form.Item>

      <Form.Item
        label="Input"
        name="input"
        rules={[{ required: true, message: "Please input!" }]}
      >
        <Input.TextArea style={{ resize: "none" }} rows={3} />
      </Form.Item>

      <Form.Item
        label="Output"
        name="output"
        rules={[{ required: true, message: "Please input!" }]}
      >
        <Input.TextArea style={{ resize: "none" }} rows={3} />
      </Form.Item>
    </>
  );
};

export default BasicInfoForm;
