import { Button, Flex, Menu, MenuProps } from "antd";
import { useState } from "react";
import { Link } from "react-router-dom";
import TabKeys, { findTabKey } from "../constants/TabKeys";
import { ProblemDetailPageData } from "../routes/problems/detail/loader";
import { EditFilled } from "@ant-design/icons";

interface ContentTapProps {
  data: ProblemDetailPageData;
  selected: (typeof TabKeys)[keyof typeof TabKeys];
}

const ContentTab = ({ data, selected }: ContentTapProps) => {
  const [current, setCurrent] = useState(selected);
  const tabs: Required<MenuProps>["items"][number][] = [
    {
      key: TabKeys.DESCRIPTION,
      label: <Link to={`/problems/${data.problemId}`}>DESCRIPTION</Link>,
    },
    {
      key: TabKeys.SUBMIT,
      label: <Link to="submit">SUBMIT</Link>,
    },
  ];

  return (
    <Flex className="w-full" align="center">
      <Menu
        onClick={(e) => setCurrent(findTabKey(e.key))}
        selectedKeys={[current]}
        mode="horizontal"
        items={tabs}
        className="flex-1"
      />
      <Button icon={<EditFilled />} size="large" className="border-0" />
    </Flex>
  );
};

export default ContentTab;
