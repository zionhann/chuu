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
        path: "/problems/:problemId",
        loader: problemDetailLoader,
        element: <ProblemDetailPage />,
        children: [
          {
            path: "/problems/:problemId",
            element: <ProblemDescriptionTab />,
          },
          {
            path: "/problems/:problemId/submit",
            element: <SolutionSubmitTab />,
          },
        ],
      },
      {
        path: "/problems/new",
        element: <ProblemAddPage />,
      },
    ],
  },
]);

export default Router;
