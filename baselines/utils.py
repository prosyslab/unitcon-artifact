import logging
import subprocess

logger = logging.getLogger(__name__)

def exec_sh(cmd, cwd, output_error=False, timeout=None):
    if isinstance(cmd, list):
        cmd = " ".join(cmd)
    logger.info(f"exec_sh: {cmd}")
    try:
        output = subprocess.check_output(cmd,
                                         timeout=timeout,
                                         stderr=subprocess.STDOUT,
                                         cwd=cwd,
                                         shell=True,
                                         executable="/bin/bash")
    except subprocess.CalledProcessError as ex:
        if output_error:
            output = ex.output
        else:
            logger.error(f"failed: {cmd}")
            logger.exception(ex)
            return
    except subprocess.TimeoutExpired as ex:
        logger.error(f"timeout: {cmd}")
        if output_error:
            output = ex.output
        else:
            logger.exception(ex)
            return
    except Exception as ex:
        logger.error(f"failed: {cmd}")
        logger.exception(ex)
        return
    return output.decode(errors='ignore')
