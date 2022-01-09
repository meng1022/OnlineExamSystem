import React from 'react';
import OverPack from 'rc-scroll-anim/lib/ScrollOverPack';
import QueueAnim from 'rc-queue-anim';
import * as pageData from './data';

export default function Page5(props) {
  return (
    <OverPack className="page-wrapper page5" id={props.id}>
      <QueueAnim type="bottom" leaveReverse key="page" className="page">
        <i key="i" />
        <h1 key="title">技能竞赛</h1>
        <p key="p"> </p>
        <div key="button" style={{ textAlign: 'center' }}>
          <a key="button" className="home-button">
            <a disabled className="ant-btn">{pageData.button}</a>
          </a>
        </div>
      </QueueAnim>
    </OverPack>
  );
}
