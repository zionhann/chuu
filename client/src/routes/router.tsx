import { createBrowserRouter } from "react-router-dom";
import Root from "./index.tsx";
import ProblemListPage from "./problems/index";
import ProblemDetailPage from "./problems/detail/index";
import ProblemAddPage from "./problems/new/index.tsx";

import problemDetailLoader from "./problems/detail/loader.tsx";
import ProblemListLoader from "./problems/loader.tsx";
import SolutionSubmitTab from "./problems/detail/tabs/submit.tsx";
import ProblemDescriptionTab from "./problems/detail/tabs/index.tsx";
import LandingPage from "./landing.tsx";
import SolutionStatusPage from "./status/index.tsx";
import SolutionListLoader from "./status/loader.tsx";
import StatusDetailLoader from "./status/detail/loader.tsx";
import StatusDetailPage from "./status/detail/index.tsx";

const Router = createBrowserRouter([
  {
    path: "/",
    element: <Root />,
    children: [
      {
        path: "/",
        element: <LandingPage />,
      },
      {
        path: "/problems",
        loader: ProblemListLoader,
        element: <ProblemListPage />,
      },
      {
        path: "/problems/:problemNumber",
        loader: problemDetailLoader,
        element: <ProblemDetailPage />,
        children: [
          {
            path: "/problems/:problemNumber",
            element: <ProblemDescriptionTab />,
          },
          {
            path: "/problems/:problemNumber/submit",
            element: <SolutionSubmitTab />,
          },
        ],
      },
      {
        path: "/problems/new",
        element: <ProblemAddPage />,
      },
      {
        path: "/status",
        element: <SolutionStatusPage />,
        loader: SolutionListLoader,
      },
      {
        path: "/status/:solutionId",
        element: <StatusDetailPage />,
        loader: StatusDetailLoader,
      },
    ],
  },
]);

export default Router;
