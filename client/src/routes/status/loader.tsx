import axiosInstance from "../../apis/axios";
import log from "../../utils/log";

export interface SolutionListResponse {
  data: {
    solutionId: number;
    submissionDate: string;
    author: string;
    problemNumber: string;
    language: string;
    verdict: string;
  }[];
}

const Loader = async ({ request }) => {
  const url = new URL(request.url);
  const problemNumber = url.searchParams.get("problemNumber") ?? undefined;
  try {
    let endpoint = "/solutions";

    if (problemNumber) {
      endpoint += `?problemNumber=${problemNumber}`;
    }
    const res: SolutionListResponse = await axiosInstance.get(endpoint);
    log.info(`GET /solutions?problemNumber=${problemNumber}`, res);
    return { res, problemNumber };
  } catch (err) {
    log.error("GET /solutions", err);
  }
};

export default Loader;
