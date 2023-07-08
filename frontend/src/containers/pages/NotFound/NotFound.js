import React from 'react';
import './NotFound.css';
import {Link} from 'react-router-dom';
import {Button} from 'antd';

function NotFound() {
  return (
      <div className="page-not-found">
        <h1 className="title">
          404
        </h1>
        <div className="desc">
          The Page you're looking for was not found.
        </div>
        <Link to="/"><Button className="go-back-btn" type="primary" size="large">Go Back</Button></Link>
      </div>
  );
}

export default NotFound;