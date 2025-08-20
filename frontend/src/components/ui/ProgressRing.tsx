interface ProgressRingProps {
  progress: number; // 0-100
  size?: 'sm' | 'md' | 'lg';
  showText?: boolean;
  children?: React.ReactNode;
}

export const ProgressRing: React.FC<ProgressRingProps> = ({ 
  progress, 
  size = 'md', 
  showText = true,
  children 
}) => {
  const sizes = {
    sm: { width: 80, strokeWidth: 6 },
    md: { width: 120, strokeWidth: 8 },
    lg: { width: 160, strokeWidth: 10 }
  };
  
  const { width, strokeWidth } = sizes[size];
  const radius = (width - strokeWidth) / 2;
  const circumference = radius * 2 * Math.PI;
  const strokeDasharray = `${circumference} ${circumference}`;
  const strokeDashoffset = circumference - (progress / 100) * circumference;
  
  return (
    <div className="relative inline-flex items-center justify-center">
      <svg width={width} height={width} className="transform -rotate-90">
        <circle
          cx={width / 2}
          cy={width / 2}
          r={radius}
          stroke="currentColor"
          strokeWidth={strokeWidth}
          fill="transparent"
          className="text-gray-200"
        />
        <circle
          cx={width / 2}
          cy={width / 2}
          r={radius}
          stroke="currentColor"
          strokeWidth={strokeWidth}
          fill="transparent"
          strokeDasharray={strokeDasharray}
          strokeDashoffset={strokeDashoffset}
          strokeLinecap="round"
          className="text-accent transition-all duration-300"
        />
      </svg>
      
      <div className="absolute inset-0 flex items-center justify-center">
        {children || (showText && (
          <div className="text-center">
            <div className="text-2xl font-bold text-gray-900">{progress}%</div>
          </div>
        ))}
      </div>
    </div>
  );
};