import { Outlet, useLoaderData } from "react-router-dom";
import { ProblemDetailPageData } from "./loader";
import ContentTab from "../../../components/contentTab";
import TabKeys from "../../../constants/TabKeys";

export const ProblemDetailPage = () => {
  const data = useLoaderData() as ProblemDetailPageData;

  return (
    <>
      <ContentTab data={data} selected={TabKeys.DESCRIPTION} />
      <Outlet context={data} />
    </>
  );
};

export default ProblemDetailPage;
