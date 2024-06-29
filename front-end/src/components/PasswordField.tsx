import {
    FormControl,
    FormLabel,
    IconButton,
    Input,
    InputGroup,
    InputProps,
    InputRightElement,
    useDisclosure,
    useMergeRefs,
} from '@chakra-ui/react'
import {forwardRef, useRef} from 'react'
import {HiEye, HiEyeOff} from 'react-icons/hi'

interface Props extends InputProps {
    passwordConfirmation?: boolean;
    currentPassword?: boolean;
}

const PasswordField = forwardRef<HTMLInputElement, Props>((props, ref) => {
    const {isOpen, onToggle} = useDisclosure()
    const inputRef = useRef<HTMLInputElement>(null)

    const mergeRef = useMergeRefs(inputRef, ref)
    const onClickReveal = () => {
        onToggle()
        if (inputRef.current) {
            inputRef.current.focus({preventScroll: true})
        }
    }

    const TYPES = {
        DEFAULT: {
            label: "Senha",
            id: "password"
        },
        PASSWORD_CONFIRMATION: {
            label: "Confirmação de senha",
            id: "passwordConfirmation"
        },
        CURRENT_PASSWORD: {
            label: "Senha atual",
            id: "currentPassword"
        }
    }

    const getType = () => {
        if (props.passwordConfirmation) return TYPES["PASSWORD_CONFIRMATION"];
        if (props.currentPassword) return TYPES["CURRENT_PASSWORD"];
        return TYPES["DEFAULT"];
    }

    return (
        <FormControl>
            <FormLabel htmlFor="password">{getType().label}</FormLabel>
            <InputGroup>
                <InputRightElement>
                    <IconButton
                        variant="text"
                        aria-label={isOpen ? 'Mask password' : 'Reveal password'}
                        icon={isOpen ? <HiEyeOff/> : <HiEye/>}
                        onClick={onClickReveal}
                    />
                </InputRightElement>
                <Input
                    id={getType().id}
                    ref={mergeRef}
                    name={getType().id}
                    type={isOpen ? 'text' : 'password'}
                    autoComplete="current-password"
                    onChange={props.onChange}
                    required
                    {...props}
                />
            </InputGroup>
        </FormControl>
    )
})

export default PasswordField;