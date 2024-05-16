const log = {
  info: (label: string, message: unknown) => {
    if (import.meta.env.DEV) {
      console.log(label, message);
    }
  },
  error: (label: string, message: unknown) => {
    if (import.meta.env.DEV) {
      console.error(label, message);
    }
  },
};

export default log;
