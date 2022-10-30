import React, {useState} from "react";

const setItem = (key, value, numberOfHours) => {
  const now = new Date();
  // set the time to be now + numberOfHours
  now.setTime(now.getTime() + (numberOfHours * 60 * 60 * 1000));
  document.cookie = `${key}=${value}; expires=${now.toUTCString()}; path=/`;
};

const getItem = key =>
    document.cookie.split("; ").reduce((total, currentCookie) => {
      const item = currentCookie.split("=");
      const storedKey = item[0];
      const storedValue = item[1];
      return key === storedKey
          ? decodeURIComponent(storedValue)
          : total;
    }, '');

const useCookie = (key, defaultValue) => {
  const getCookie = () => getItem(key) || defaultValue;
  const [cookie, setCookie] = useState(getCookie());
  const updateCookie = (value, numberOfHours) => {
    setCookie(value);
    setItem(key, value, numberOfHours);
  };
  return [cookie, updateCookie];
};

const BrowserUtils = {
  useCookie
}

export default BrowserUtils;