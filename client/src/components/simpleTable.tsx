import { Table } from "antd";
import type { TableProps } from "antd";

export interface DataType {
  key: string;
}

interface SimpleTableProps {
  columns: TableProps["columns"];
  dataSource: DataType[];
}

const SimpleTable = ({ columns, dataSource }: SimpleTableProps) => (
  <Table columns={columns} dataSource={dataSource} />
);

export default SimpleTable;
