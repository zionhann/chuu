import { CloseOutlined } from "@ant-design/icons";
import { Button, Card, Flex, Form, Input, Typography } from "antd";

const ExampleForm = () => {
  const form = Form.useFormInstance();

  return (
    <>
      <Form.List
        name="testCases"
        rules={[
          {
            validator: async (_, names) => {
              if (!names || names.length < 1) {
                return Promise.reject(new Error("At least 1 test case."));
              }
            },
          },
        ]}
      >
        {(fields, { add, remove }, { errors }) => (
          <div style={{ display: "flex", rowGap: 16, flexDirection: "column" }}>
            {fields.map((field) => (
              <Card
                size="small"
                title={`Example ${field.name + 1}`}
                key={field.key}
                extra={
                  <CloseOutlined
                    onClick={() => {
                      remove(field.name);
                    }}
                  />
                }
              >
                <Form.Item
                  label="Name"
                  name={[field.name, "number"]}
                  initialValue={field.name + 1}
                  hidden
                >
                  <Input />
                </Form.Item>

                <Form.Item label="Inputs">
                  <Form.List name={[field.name, "inputs"]} initialValue={[]}>
                    {(subFields, subOpt) => (
                      <div
                        style={{
                          display: "flex",
                          flexDirection: "column",
                          rowGap: 16,
                        }}
                      >
                        {subFields.map((subField) => (
                          <Flex key={subField.key}>
                            <Form.Item
                              noStyle
                              name={[subField.name]}
                              rules={[
                                { required: true, message: "Please input!" },
                              ]}
                              initialValue={""}
                            >
                              <Input placeholder="input" />
                            </Form.Item>
                            <CloseOutlined
                              onClick={() => {
                                subOpt.remove(subField.name);
                              }}
                              className="pl-2"
                            />
                          </Flex>
                        ))}
                        <Button
                          type="dashed"
                          onClick={() => subOpt.add()}
                          block
                        >
                          + Add Input
                        </Button>
                      </div>
                    )}
                  </Form.List>
                </Form.Item>

                <Form.Item
                  label="Output"
                  name={[field.name, "output"]}
                  rules={[{ required: true, message: "Please input!" }]}
                  initialValue={""}
                >
                  <Input.TextArea style={{ resize: "none" }} rows={3} />
                </Form.Item>
              </Card>
            ))}

            <Button type="dashed" onClick={() => add()} block>
              + Add Example
            </Button>
            <Form.ErrorList errors={errors} className="text-err" />
          </div>
        )}
      </Form.List>

      <Form.Item noStyle shouldUpdate>
        {() => (
          <Typography>
            <pre>{JSON.stringify(form.getFieldsValue(), null, 2)}</pre>
          </Typography>
        )}
      </Form.Item>
    </>
  );
};

export default ExampleForm;
