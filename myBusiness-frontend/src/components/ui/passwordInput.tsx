import React, { useEffect, useState, ForwardedRef } from 'react';
import { Eye, EyeOff, Lock as LockIcon } from 'lucide-react';

interface PasswordInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  error?: string;
  showStrength?: boolean;
}

const strengthLabels = ['Débil','Medio','Fuerte'];
const strengthColors = ['bg-red-500','bg-yellow-400','bg-green-600'];

const PasswordInput = React.forwardRef(function PasswordInput(
  {
    error,
    showStrength = true,
    ...props
  }: PasswordInputProps,
  ref: ForwardedRef<HTMLInputElement>
) {
  // 1) Usamos un estado interno para controlar si se muestra la contraseña
  const [visible, setVisible] = useState(false);

  // 2) Valor actual del input (para el zxcvbn)
  const value = (props.value as string) || '';

  // 3) Score de zxcvbn, si showStrength está activado
  const [score, setScore] = useState<0 | 1 | 2 | 3 | 4>(0);
useEffect(() => {
   if (showStrength) {
     setScore(computeStrength(value));
   }
 }, [value, showStrength]);

  return (
    <div className="w-full">
      <div className="relative mt-1">
        {/* candado a la izquierda */}
        <LockIcon className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />

        {/* 4) El tipo de input depende del estado interno "visible" */}
        <input
          {...props}
          ref={ref}
          type={visible ? 'text' : 'password'}
          className={`w-full h-12 border ${error ? 'border-red-500' : 'border-gray-300'} rounded pl-10 pr-10 focus:outline-none focus:ring-2 focus:ring-blue-500`}
        />

        {/* ojo a la derecha, que simplemente alterna "visible" */}
        <button
          type="button"
          onClick={() => setVisible((v) => !v)}
          className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
        >
          {visible ? <EyeOff size={20} /> : <Eye size={20} />}
        </button>
      </div>

      {showStrength && (
        <>
          <div className="flex mt-1 space-x-1 h-1">
            {strengthColors.map((c, i) => (
   <div key={i} className={`flex-1 ${i <= score ? c : 'bg-gray-200'} rounded-sm`} />
 ))}
          </div>
          <p className="form-feedback form-feedback--hint mt-1">{strengthLabels[score]}</p>
        </>
      )}

      {error && <p className="form-feedback form-feedback--error mt-1">{error}</p>}
    </div>
  );
}); // 

function computeStrength(pass: string): 0|1|2 {
  if (pass.length === 0) return 0; // vacío/weak
  const hasLower = /[a-z]/.test(pass);
  const hasUpper = /[A-Z]/.test(pass);
  const numCount = (pass.match(/\d/g) || []).length;
  const hasSpecial = /[^A-Za-z0-9]/.test(pass);

  if (hasLower && hasUpper && numCount >= 3 && hasSpecial) {
    return 2; // fuerte
  }
  if (hasLower && hasUpper && numCount >= 1) {
    return 1; // medio
  }
  return 0;   // débil
}

export default PasswordInput;
