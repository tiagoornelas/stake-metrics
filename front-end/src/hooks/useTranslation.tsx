import { useContext } from 'react';
import { TranslationContext } from '../providers/TranslationProvider';

const useTranslation = () => useContext(TranslationContext);

export default useTranslation;
