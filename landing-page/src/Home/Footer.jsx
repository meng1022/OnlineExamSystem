import React from 'react';
import * as pageData from './data';

const { footer } = pageData;

export default function Footer(props) {
  return (
    <footer className="footer">
      {props.isMobile ? null : (
        <div className="links">
            技术支持：<a href="http://www.hkangcloud.com/" className="HCloud" >北京海康云翔科技有限公司</a>
          </div>)}
      <span className="logo" key="logo" />
      <span key="logo-name">{footer.logoName}</span>
      <i key="slice" />
      <span>{props.isMobile ? footer.name.split('©').pop() : footer.name}</span>
    </footer>
  );
}
