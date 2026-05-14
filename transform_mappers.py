import re
import os

files = [
    'CategoryMapper.xml', 'DashboardMapper.xml', 'EmpAttendanceMapper.xml', 
    'EmployeeMapper.xml', 'LogMapper.xml', 'MemberMapper.xml', 
    'ModalMapper.xml', 'PostMapper.xml', 'ProductMapper.xml', 
    'PtRegistrationMapper.xml', 'SalesAnalyticsMapper.xml', 'SalesItemMapper.xml', 
    'SalesServiceMapper.xml', 'ScheduleMapper.xml', 'ServiceMapper.xml', 
    'StockMapper.xml'
]

base_path = r'C:\playground\git_repos\GymErp\src\main\resources\mapper\\'

def process_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Split by XML tags to separate tags from SQL/text
    parts = re.split(r'(<[^>]+>)', content)
    new_parts = []
    for part in parts:
        if part.startswith('<'):
            # This is an XML tag. 
            # Lowercase 'column' attribute value if present.
            new_tag = re.sub(r'(\bcolumn=")([^"]+)(")', lambda m: m.group(1) + m.group(2).lower() + m.group(3), part)
            new_parts.append(new_tag)
        else:
            # This is a text node (SQL block).
            def sub_sql(m):
                s = m.group(0)
                if s.startswith("'") or s.startswith('#') or s.startswith('$') or s.startswith('jdbcType='):
                    return s
                return s.lower()
            
            # Regex to match:
            # - String literals: '[^']*'
            # - Placeholders: [#$]\{[^\}]+\}
            # - jdbcType=... : jdbcType=[A-Z]+
            # - Words: [A-Za-z0-9_]+
            new_sql = re.sub(r"'[^']*'|[#$]\{[^\}]+\}|jdbcType=[A-Z]+|[A-Za-z0-9_]+", sub_sql, part)
            new_parts.append(new_sql)
            
    transformed = "".join(new_parts)
    
    with open(file_path, 'w', encoding='utf-8', newline='\n') as f:
        f.write(transformed)

for file_name in files:
    full_path = os.path.join(base_path, file_name)
    try:
        process_file(full_path)
        print(f"Successfully processed {file_name}")
    except Exception as e:
        print(f"Error processing {file_name}: {e}")
