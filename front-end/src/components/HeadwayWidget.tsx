import { Button } from "@chakra-ui/react";
import { useEffect } from "react";
import { HiSparkles } from "react-icons/hi";

declare global {
  interface Window {
    Headway: any;
    headwayScriptLoaded?: boolean;
  }
}

const HeadwayWidget = () => {
  const initHeadway = () => {
    window.Headway?.init({
      selector: ".headway-widget",
      trigger: ".headway-trigger",
      account: "J4rpZx",
      translations: {
        title: "Novidades do Stake Metrics ✨",
        readMore: "Leia mais",
        labels: {
          new: "Novo",
          improvement: "Melhoria",
          fix: "Correção",
        },
        footer: "Confira todas as atualizações 👉",
      },
    });
  };

  useEffect(() => {
    const style = document.createElement("style");
    style.innerHTML = `
      .headway-widget {
        position: relative;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 2px;
      }

      #HW_badge_cont {
        position: absolute !important;
        top: -8px;
        right: -8px;
        z-index: 10;
      }
    `;
    document.head.appendChild(style);

    if (!window.headwayScriptLoaded) {
      const script = document.createElement("script");
      script.src = "//cdn.headwayapp.co/widget.js";
      script.async = true;
      script.onload = () => initHeadway();
      document.body.appendChild(script);
      window.headwayScriptLoaded = true;
    } else {
      initHeadway();
    }
  }, []);

  return (
    <Button className="headway-widget headway-trigger" aria-label="Novidades">
      <HiSparkles />
    </Button>
  );
};

export default HeadwayWidget;
