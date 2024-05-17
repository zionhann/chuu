import { Form, Input } from "antd";

const BasicInfoForm = () => {
  return (
    <>
      <Form.Item
        label="Problem Number"
        name="number"
        rules={[{ required: true, message: "Please input!" }]}
        wrapperCol={{ span: 5 }}
      >
        <Input size="large" />
      </Form.Item>
      <Form.Item
        label="Problem Name"
        name="name"
        rules={[{ required: true, message: "Please input!" }]}
      >
        <Input size="large" />
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
        name="inputDescription"
        rules={[{ required: true, message: "Please input!" }]}
      >
        <Input.TextArea style={{ resize: "none" }} rows={3} />
      </Form.Item>

      <Form.Item
        label="Output"
        name="outputDescription"
        rules={[{ required: true, message: "Please input!" }]}
      >
        <Input.TextArea style={{ resize: "none" }} rows={3} />
      </Form.Item>
    </>
  );
};

export default BasicInfoForm;
