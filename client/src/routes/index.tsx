import { useState } from "react";
import BaseLayout from "../components/baseLayout";
import PageKeys from "../constants/pageKeys";
import { Outlet } from "react-router-dom";

const Root = () => {
  const [currentPage, setCurrentPage] = useState(PageKeys.HOME);

  return (
    <BaseLayout pageKey={currentPage}>
      <Outlet context={setCurrentPage} />
    </BaseLayout>
  );
};

export default Root;
