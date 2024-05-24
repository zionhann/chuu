import { CloseOutlined } from "@ant-design/icons";
import { Button, Card, Flex, Form, Input, Switch, Typography } from "antd";
import { useState } from "react";

const ExampleForm = () => {
  const form = Form.useFormInstance();
  const [disabledKeys, setDisabledKeys] = useState<number[]>([]);

  const onChange = (key: number, isChecked: boolean) => {
    if (isChecked) {
      setDisabledKeys(disabledKeys.filter((k) => k !== key));
      return;
    }
    setDisabledKeys([...disabledKeys, key]);
  };

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
            {fields.map((field) => {
              const isRequired = !disabledKeys.includes(field.key);

              return (
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
                  <Flex gap={10} className="pb-2">
                    <label>Input</label>
                    <Switch
                      onChange={(isChecked) => onChange(field.key, isChecked)}
                      defaultChecked
                    />
                  </Flex>
                  <Form.Item
                    name={[field.name, "input"]}
                    rules={[{ required: isRequired, message: "Please input!" }]}
                  >
                    <Input.TextArea
                      style={{ resize: "none", minHeight: 96 }}
                      disabled={disabledKeys.includes(field.key)}
                      autoSize
                    />
                  </Form.Item>

                  <Form.Item
                    label="Output"
                    name={[field.name, "output"]}
                    rules={[{ required: true, message: "Please input!" }]}
                  >
                    <Input.TextArea
                      style={{ resize: "none", minHeight: 96 }}
                      autoSize
                    />
                  </Form.Item>
                </Card>
              );
            })}

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
