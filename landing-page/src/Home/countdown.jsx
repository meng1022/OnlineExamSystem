import React, {Component} from 'react'
import moment from 'moment';

export default class countDown extends Component {
    state = {
        days: undefined,
        hours: undefined,
        minutes: undefined,
        seconds: undefined
    };

    componentDidMount() {
        this.interval = setInterval(() => {
            const { timeTillDate, timeFormat } = this.props;
            const then = moment(timeTillDate, timeFormat);
            const now = moment();
            const countdown = moment.duration(then.diff(now));
            const days = String(countdown.days());
            const hours = String(countdown.hours());
            const minutes = String(countdown.minutes());
            const seconds = String(countdown.seconds());

            this.setState({ days, hours, minutes, seconds });
        }, 1000);
    }

    componentWillUnmount() {
        if (this.interval) {
            clearInterval(this.interval);
        }
    }

    render() {
        const { days, hours, minutes, seconds } = this.state;
        const {isMobile} = this.props

        // Mapping the date values to radius values
        const daysRadius = mapNumber(days, 30, 0, 0, 360);
        const hoursRadius = mapNumber(hours, 24, 0, 0, 360);
        const minutesRadius = mapNumber(minutes, 60, 0, 0, 360);
        const secondsRadius = mapNumber(seconds, 60, 0, 0, 360);

        if (!seconds) {
            return null;
        }

        return (
                <div className="countdown-wrapper">
                    {days && (
                        <div className={isMobile ? 'countdown-item1' : 'countdown-item'}>
                            <SVGCircle radius={daysRadius} isMobile={isMobile} />
                            {days}
                            <span>天</span>
                        </div>
                    )}
                    {hours && (
                        <div className={isMobile ? 'countdown-item1' : 'countdown-item'}>
                            <SVGCircle radius={hoursRadius} isMobile={isMobile} />
                            {hours}
                            <span>小时</span>
                        </div>
                    )}
                    {minutes && (
                        <div className={isMobile ? 'countdown-item1' : 'countdown-item'}>
                            <SVGCircle radius={minutesRadius} isMobile={isMobile} />
                            {minutes}
                            <span>分</span>
                        </div>
                    )}
                    {seconds && (
                        <div className={isMobile ? 'countdown-item1' : 'countdown-item'}>
                            <SVGCircle radius={secondsRadius} isMobile={isMobile} />
                            {seconds}
                            <span>秒</span>
                        </div>
                    )}
                </div>
        );
    }
}
const SVGCircle = ({ radius, isMobile }) => {
  if (isMobile) {
    return (
        <svg className="countdown-svg1">
            <path
                fill="none"
                stroke="#fff"
                strokeWidth="3"
                d={describeArc(30, 30, 28.2, 0, radius)}
            />
        </svg>
    )
  } else {
    return (
      <svg className="countdown-svg">
          <path
              fill="none"
              stroke="#fff"
              strokeWidth="4"
              d={describeArc(50, 50, 48, 0, radius)}
          />
      </svg>
    )
  }
};

// From StackOverflow: https://stackoverflow.com/questions/5736398/how-to-calculate-the-svg-path-for-an-arc-of-a-circle
function polarToCartesian(centerX, centerY, radius, angleInDegrees) {
    var angleInRadians = ((angleInDegrees - 90) * Math.PI) / 180.0;

    return {
        x: centerX + radius * Math.cos(angleInRadians),
        y: centerY + radius * Math.sin(angleInRadians)
    };
}

function describeArc(x, y, radius, startAngle, endAngle) {
    var start = polarToCartesian(x, y, radius, endAngle);
    var end = polarToCartesian(x, y, radius, startAngle);

    var largeArcFlag = endAngle - startAngle <= 180 ? '0' : '1';

    var d = [
        'M',
        start.x,
        start.y,
        'A',
        radius,
        radius,
        0,
        largeArcFlag,
        0,
        end.x,
        end.y
    ].join(' ');

    return d;
}

// From StackOverflow: https://stackoverflow.com/questions/10756313/javascript-jquery-map-a-range-of-numbers-to-another-range-of-numbers
function mapNumber(number, in_min, in_max, out_min, out_max) {
    return (
        ((number - in_min) * (out_max - out_min)) / (in_max - in_min) + out_min
    );
}