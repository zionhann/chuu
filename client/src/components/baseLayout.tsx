import { Layout, Menu, theme } from "antd";
import { Link } from "react-router-dom";
import PageKeys from "../constants/pageKeys";

interface BaseLayoutProps {
  pageKey: (typeof PageKeys)[keyof typeof PageKeys];
  children?: React.ReactNode;
}

const BaseLayout = ({ pageKey, children }: BaseLayoutProps) => {
  const { Header, Content, Footer } = Layout;
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const pages = [
    {
      key: PageKeys.HOME,
      label: <Link to="/">HOME</Link>,
    },
    {
      key: PageKeys.PROBLEMS,
      label: <Link to="/problems">PROBLEMS</Link>,
    },
  ];

  return (
    <Layout className="h-fit min-h-screen bg-white">
      <Header
        style={{
          position: "sticky",
          top: 0,
          zIndex: 1,
          width: "100%",
          display: "flex",
          alignItems: "center",
        }}
      >
        <div className="demo-logo" />
        <Menu
          theme="dark"
          mode="horizontal"
          defaultSelectedKeys={[pageKey]}
          items={pages}
          style={{ flex: 1, minWidth: 0 }}
        />
        {/* TODO: Add Sign In Button */}
        {/* <Link to="/sign-in">
          <Typography.Text className="text-white text-base">
            Sign In
          </Typography.Text>
        </Link> */}
      </Header>
      <Content className="w-screen max-w-screen-lg mx-auto py-4">
        <div
          style={{
            padding: 24,
            // minHeight: 380,
            background: colorBgContainer,
            borderRadius: borderRadiusLG,
          }}
        >
          {children}
        </div>
      </Content>
      <Footer style={{ textAlign: "center" }}>
        Ant Design ©{new Date().getFullYear()} Created by Ant UED
      </Footer>
    </Layout>
  );
};

export default BaseLayout;
