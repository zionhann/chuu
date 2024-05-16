const log = {
  info: (message: unknown) => {
    if (import.meta.env.DEV) {
      console.log(message);
    }
  },
  error: (message: unknown) => {
    if (import.meta.env.DEV) {
      console.error(message);
    }
  },
};

export default log;
